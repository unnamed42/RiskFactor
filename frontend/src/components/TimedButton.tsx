import React, { FC, useState } from "react";

import { Button } from "antd";
import { ButtonProps } from "antd/es/button";

import { sleep } from "@/utils";

interface P extends ButtonProps {
  interval: number;
  text?: string;
}

/**
 * 按钮按下后，等待一段时间才能再次click
 * 可用于请求验证码的按钮，请求一次之后60s以内不能再次请求
 */
export const TimedButton: FC<P> = ({ interval, text, ...props }) => {
  const [waiting, setWaiting] = useState(false);
  const [timer, setTimer] = useState(0);

  const clicked = async () => {
    if (waiting) return;
    setWaiting(true);
    for (let sec = interval; sec !== 0; --sec) {
      setTimer(sec);
      await sleep(1000);
    }
    setWaiting(false);
  };

  return <Button loading={waiting} disabled={waiting} onClick={clicked} {...(props as ButtonProps)}>
    {waiting ? `${timer}秒` : text}
  </Button>;
};
