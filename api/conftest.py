"""
pytest shared fixtures and parametrize helpers for the bowling scoring API.

Run with:
    pip install -r api/requirements.txt
    pytest api/ -v
"""

import pytest
from fastapi.testclient import TestClient

from api.main import app  # adjust import path if main.py is at repo root


# ---------------------------------------------------------------------------
# Shared client fixture
# ---------------------------------------------------------------------------

@pytest.fixture(scope="module")
def client() -> TestClient:
    """Provide a TestClient bound to the FastAPI app for the entire module."""
    return TestClient(app)


# ---------------------------------------------------------------------------
# Parametrize data: (description, rolls, expected_score)
# ---------------------------------------------------------------------------

SCORING_CASES = [
    pytest.param(
        "gutter game",
        [0] * 20,
        0,
        id="gutter_game",
    ),
    pytest.param(
        "all ones",
        [1] * 20,
        20,
        id="all_ones",
    ),
    pytest.param(
        "all fours",
        [4] * 20,
        80,
        id="all_fours",
    ),
    pytest.param(
        "perfect game",
        [10] * 12,
        300,
        id="perfect_game",
    ),
    pytest.param(
        "spare then 3",
        [5, 5, 3] + [0] * 17,
        16,
        id="spare_bonus",
    ),
    pytest.param(
        "strike then 3+4",
        [10, 3, 4] + [0] * 16,
        24,
        id="strike_bonus",
    ),
    pytest.param(
        "all spares with final 10",
        [5] * 20 + [10],
        155,
        id="all_spares",
    ),
]


@pytest.fixture(params=SCORING_CASES)
def scoring_case(request):
    """Parametrized fixture providing (description, rolls, expected_score) tuples."""
    return request.param


# ---------------------------------------------------------------------------
# Convenience: roll helpers (usable in tests via direct import)
# ---------------------------------------------------------------------------

def make_rolls_perfect() -> list[int]:
    return [10] * 12


def make_rolls_gutter() -> list[int]:
    return [0] * 20


def make_rolls_all_ones() -> list[int]:
    return [1] * 20
