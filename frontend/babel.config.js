module.exports = {
    presets: [
        ["@babel/preset-env"],
        "@babel/preset-react",
        "@babel/preset-typescript"
    ],
    plugins: [
        "@babel/plugin-proposal-optional-chaining",
        "@babel/plugin-syntax-dynamic-import",
        "@babel/proposal-class-properties",
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