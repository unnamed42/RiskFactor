module.exports = api => {
  api.cache(true);

  const presets = [
    ["@babel/preset-env", {
      modules: false,
      exclude: [
        "transform-regenerator",
        "transform-async-to-generator"
      ]
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
      corejs: 3,
      useESModules: true
    }],
    "lodash",
    ["import", { libraryName: "antd", libraryDirectory: "es", style: true }, "antd"]
  ];

  return { presets, plugins };
};
