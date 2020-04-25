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
