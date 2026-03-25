import { calculateScore } from "./index";

describe("Bowling scoring", () => {
  test("gutter game scores 0", () => {
    expect(calculateScore(new Array(20).fill(0)).score).toBe(0);
  });

  test("all ones scores 20", () => {
    expect(calculateScore(new Array(20).fill(1)).score).toBe(20);
  });

  test("perfect game scores 300", () => {
    expect(calculateScore(new Array(12).fill(10)).score).toBe(300);
    expect(calculateScore(new Array(12).fill(10)).isPerfectGame).toBe(true);
  });

  test("spare followed by 3 scores 16", () => {
    expect(calculateScore([5, 5, 3, ...new Array(17).fill(0)]).score).toBe(16);
  });

  test("strike followed by 3,4 scores 24", () => {
    expect(calculateScore([10, 3, 4, ...new Array(16).fill(0)]).score).toBe(24);
  });

  test("frames are built correctly for perfect game", () => {
    const { frames } = calculateScore(new Array(12).fill(10));
    expect(frames).toHaveLength(10);
    frames.forEach((f) => expect(f.type).toBe("strike"));
  });
});
