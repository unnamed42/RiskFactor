module.exports = {
  module: {
    rules: [{
      enforce: "pre",
      include: [require.resolve("@ant-design/icons/lib/dist")],
      loader: "webpack-ant-icon-loader"
    }]
  },
  optimization: {
    splitChunks: {
      chunks: chunk => chunk.name !== "antd-icons"
    }
  }
};