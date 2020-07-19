declare module "*.less" {
  const style: { [name: string]: string };
  export default style;
}

declare module "*.css" {
  const style: { [name: string]: string };
  export default style;
}

declare module "*.png" {
  const path: string;
  export default path;
}

/**
 * 空类型，里面什么都没有
 *
 * 纯粹为了绕开`@typescript-eslint/ban-types`，因为`{}`代表任何非`null`/`undefined`，并非空类型
 */
// eslint-disable-next-line @typescript-eslint/no-empty-interface
declare interface Empty {}
