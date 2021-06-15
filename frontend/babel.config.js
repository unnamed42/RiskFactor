module.exports = {
  presets: [
    ["@babel/preset-env", {
      exclude: [
        "transform-regenerator",
        "transform-async-to-generator"
      ]
    }],
    ["@babel/preset-react", {
      development: process.env.NODE_ENV === "development"
    }],
    ["@babel/preset-typescript", {
      onlyRemoveTypeImports: true // recommended for fork-ts-webpack-plugin
    }]
  ],
  plugins: [
    ["@babel/plugin-proposal-decorators", { legacy: true }],
    "babel-plugin-transform-async-to-promises",
    // enables this to use more modern features
    ["@babel/plugin-transform-runtime", {
      regenerator: false,
      corejs: 3,
      useESModules: true
    }],
    "lodash",
    ["import", { libraryName: "antd", libraryDirectory: "es", style: true }, "antd"]
  ]
};
