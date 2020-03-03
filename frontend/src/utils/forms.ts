import { get } from "lodash";
import { Store } from "rc-field-form/es/interface";

export const enablerFId = (parentId: string | number) => `#enabler.$${parentId}`;

export const shouldUpdate = (namePath: Array<string | number> | string | number) =>
  (prev: Store, curr: Store) => get(prev, namePath) == get(curr, namePath);
