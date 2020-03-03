module.exports = api => {
  api.cache(true);

  const presets = [
    ["@babel/preset-env", {
      targets: {
        ie: "11"
      }
    }],
    "@babel/preset-react",
    "@babel/preset-typescript"
  ];

  const plugins = [
    ["@babel/plugin-transform-typescript", { allowDeclareFields: true }],
    ["@babel/plugin-proposal-decorators", { legacy: true }],
    ["@babel/plugin-proposal-class-properties", { loose: true }],
    "@babel/proposal-object-rest-spread",
    "babel-plugin-transform-async-to-promises",
    // enables this to use more modern features
    ["@babel/plugin-transform-runtime", {
      regenerator: false,
      useESModules: true
      // corejs: 3
    }],
    "lodash",
    ["import", { libraryName: "antd", libraryDirectory: "es", style: true }, "antd"]
  ];

  return { presets, plugins };
}
