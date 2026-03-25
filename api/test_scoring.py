"""pytest tests for the FastAPI scoring endpoint."""
import pytest
from fastapi.testclient import TestClient
from api.main import app

client = TestClient(app)


def test_health():
    r = client.get("/health")
    assert r.status_code == 200
    assert r.json()["status"] == "ok"


def test_gutter_game():
    r = client.post("/score", json={"player_name": "Gutter", "rolls": [0] * 20})
    assert r.status_code == 200
    assert r.json()["score"] == 0


def test_all_ones():
    r = client.post("/score", json={"player_name": "One", "rolls": [1] * 20})
    assert r.status_code == 200
    assert r.json()["score"] == 20


def test_perfect_game():
    r = client.post("/score", json={"player_name": "Perfect", "rolls": [10] * 12})
    assert r.status_code == 200
    assert r.json()["score"] == 300
    assert r.json()["is_perfect_game"] is True


def test_spare():
    rolls = [5, 5, 3] + [0] * 17
    r = client.post("/score", json={"player_name": "Spare", "rolls": rolls})
    assert r.status_code == 200
    assert r.json()["score"] == 16


def test_invalid_roll_too_high():
    r = client.post("/score", json={"player_name": "Bad", "rolls": [11] + [0] * 19})
    assert r.status_code == 400
