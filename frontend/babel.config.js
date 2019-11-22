module.exports = {
    presets: [
        ["@babel/preset-env", {
            targets: {
                ie: "11"
            }
        }],
        "@babel/preset-react",
        "@babel/preset-typescript"
    ],
    plugins: [
        "@babel/plugin-proposal-optional-chaining",
        "@babel/plugin-proposal-nullish-coalescing-operator",
        "@babel/plugin-syntax-dynamic-import",
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
    ]
};
