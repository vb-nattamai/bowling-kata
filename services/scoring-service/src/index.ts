/**
 * Bowling Kata — TypeScript/Express scoring microservice.
 * Wraps the core bowling logic and exposes it over HTTP.
 */
import express, { Request, Response } from "express";

const app = express();
app.use(express.json());

export interface ScoreResult {
  score: number;
  frames: Frame[];
  isPerfectGame: boolean;
}

export interface Frame {
  index: number;
  rolls: number[];
  type: "strike" | "spare" | "open";
  frameScore: number;
}

export function calculateScore(rolls: number[]): ScoreResult {
  let score = 0;
  let idx = 0;
  const frames: Frame[] = [];

  for (let frame = 0; frame < 10; frame++) {
    if (idx >= rolls.length) break;

    if (rolls[idx] === 10) {
      const bonus = (rolls[idx + 1] ?? 0) + (rolls[idx + 2] ?? 0);
      const frameScore = 10 + bonus;
      score += frameScore;
      frames.push({ index: frame + 1, rolls: [10], type: "strike", frameScore });
      idx++;
    } else if ((rolls[idx] ?? 0) + (rolls[idx + 1] ?? 0) === 10) {
      const bonus = rolls[idx + 2] ?? 0;
      const frameScore = 10 + bonus;
      score += frameScore;
      frames.push({ index: frame + 1, rolls: [rolls[idx], rolls[idx + 1]], type: "spare", frameScore });
      idx += 2;
    } else {
      const frameScore = (rolls[idx] ?? 0) + (rolls[idx + 1] ?? 0);
      score += frameScore;
      frames.push({ index: frame + 1, rolls: [rolls[idx] ?? 0, rolls[idx + 1] ?? 0], type: "open", frameScore });
      idx += 2;
    }
  }

  return { score, frames, isPerfectGame: score === 300 };
}

app.get("/health", (_req: Request, res: Response) => {
  res.json({ status: "ok", service: "bowling-scoring-service" });
});

app.post("/score", (req: Request, res: Response) => {
  const { rolls, playerName } = req.body as { rolls: number[]; playerName: string };

  if (!Array.isArray(rolls) || rolls.some((r) => r < 0 || r > 10)) {
    return res.status(400).json({ error: "Invalid rolls" });
  }

  const result = calculateScore(rolls);
  res.json({ playerName, ...result });
});

const PORT = process.env.PORT ?? 3000;
app.listen(PORT, () => console.log(`Scoring service running on :${PORT}`));

export default app;
