import React from "react";
import { withRouter } from "react-router-dom";

import { Result, Button } from "antd";

export const Success = withRouter(({ history, ...props}) => {

  return (
    <Result {...props} status="success"
      title="提交成功"
      extra={[
        <Button type="primary" onClick={() => history.push("/")}>返回主页</Button>
      ]}
    />
  );
});

export default Success;
