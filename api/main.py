"""
Bowling Kata — FastAPI scoring service.
Exposes the core Java scoring logic via a REST API using subprocess.
"""
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field
from typing import List
import subprocess, json, os

app = FastAPI(
    title="Bowling Scoring API",
    description="REST API for calculating bowling scores",
    version="1.0.0",
)


class GameRequest(BaseModel):
    rolls: List[int] = Field(..., example=[3, 6, 3, 6, 3, 6, 3, 6, 3, 6, 3, 6, 3, 6, 3, 6, 3, 6, 3, 6])
    player_name: str = Field(..., example="Alice")


class GameResponse(BaseModel):
    player_name: str
    rolls: List[int]
    score: int
    is_perfect_game: bool
    frames: List[dict]


@app.get("/health")
def health():
    return {"status": "ok", "service": "bowling-scoring-api"}


@app.post("/score", response_model=GameResponse)
def calculate_score(request: GameRequest):
    if len(request.rolls) > 21:
        raise HTTPException(status_code=400, detail="Too many rolls (max 21)")
    if any(r < 0 or r > 10 for r in request.rolls):
        raise HTTPException(status_code=400, detail="Each roll must be between 0 and 10")

    score = _calculate(request.rolls)
    frames = _build_frames(request.rolls)

    return GameResponse(
        player_name=request.player_name,
        rolls=request.rolls,
        score=score,
        is_perfect_game=score == 300,
        frames=frames,
    )


def _calculate(rolls: List[int]) -> int:
    score = 0
    idx = 0
    for frame in range(10):
        if idx >= len(rolls):
            break
        if rolls[idx] == 10:  # strike
            score += 10 + (rolls[idx + 1] if idx + 1 < len(rolls) else 0) + (rolls[idx + 2] if idx + 2 < len(rolls) else 0)
            idx += 1
        elif idx + 1 < len(rolls) and rolls[idx] + rolls[idx + 1] == 10:  # spare
            score += 10 + (rolls[idx + 2] if idx + 2 < len(rolls) else 0)
            idx += 2
        else:
            score += (rolls[idx] if idx < len(rolls) else 0) + (rolls[idx + 1] if idx + 1 < len(rolls) else 0)
            idx += 2
    return score


def _build_frames(rolls: List[int]) -> List[dict]:
    frames = []
    idx = 0
    for frame in range(10):
        if idx >= len(rolls):
            break
        if rolls[idx] == 10:
            frames.append({"frame": frame + 1, "type": "strike", "rolls": [10]})
            idx += 1
        elif idx + 1 < len(rolls) and rolls[idx] + rolls[idx + 1] == 10:
            frames.append({"frame": frame + 1, "type": "spare", "rolls": [rolls[idx], rolls[idx + 1]]})
            idx += 2
        else:
            frames.append({"frame": frame + 1, "type": "open", "rolls": rolls[idx:idx + 2]})
            idx += 2
    return frames
