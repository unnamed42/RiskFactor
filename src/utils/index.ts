import { useState } from "react";

// wrapper around useState, report new value after setState
const useStateAsync = <T> (init: T): [T, (x: T) => Promise<T> ] => {
    const [value, setValue] = useState(init);
    const setter = (x: T) => new Promise<T>(resolve => {
        setValue(x); resolve(x);
    });
    return [value, setter];
};

const sleep = (ms: number) => {
    return new Promise(resolve => setTimeout(resolve, ms));
};

export { useStateAsync, sleep };
