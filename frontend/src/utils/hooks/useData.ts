import { useEffect, useState, Dispatch, SetStateAction } from "react";

import type { UseApiDom } from "./useApi";

export const useData = <T>(source: UseApiDom<T>): [T | undefined, Dispatch<SetStateAction<T | undefined>>] => {
  const [state, setState] = useState<T>();

  useEffect(() => {
    if (source.alt === undefined)
      setState(source.data);
  }, [source]);

  return [state, setState];
};
