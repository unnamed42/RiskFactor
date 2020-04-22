import { useCallback } from "react";
import { get } from "lodash";

import type { NamePath, Store } from "rc-field-form/es/interface";

type Checker = (prev: Store, curr: Store) => boolean;

export const fieldUpdated = (path: NamePath): Checker => {
  return (prev, curr) => get(prev, path) == get(curr, path);
}

export const useFieldUpdated = (path: NamePath): Checker => useCallback(fieldUpdated(path), [path]);
