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
    "@babel/preset-typescript"
  ],
  plugins: [
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
  ]
};
