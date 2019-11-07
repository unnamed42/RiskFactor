module.exports = {
  module: {
    rules: [
      {
        test: /\.(png|svg|jpe?g|gif)$/,
        use: ["file-loader"]
      },
      {
        test: /\.(woff2?|eot|ttf|otf)$/,
        use: ["file-loader"]
      }
    ]
  }
};
