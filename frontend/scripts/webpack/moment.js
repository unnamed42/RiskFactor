const { ContextReplacementPlugin } = require("webpack");

module.exports = {
  resolve: {
    alias: {
      moment: "dayjs"
    }
  },
  // plugins: [
  //   // remove unneeded locales from moment.js
  //   new ContextReplacementPlugin(/moment[\/\\]locale$/, /zh-cn/),
  // ]
};
