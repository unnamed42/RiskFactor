import { useState } from "react";

// wrapper around useState, report new value after setState
export const useStateAsync = <T>(init?: T | undefined): [T | undefined, (x: T) => Promise<T>] => {
  const [value, setValue] = useState(init);
  return [value, async (x: T) => { setValue(x); return x; }];
};

export const sleep = (ms: number) =>
  new Promise(resolve => setTimeout(resolve, ms));
