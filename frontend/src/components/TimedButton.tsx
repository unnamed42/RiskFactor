import React, { FC, useState, useEffect } from "react";

import { Button } from "antd";
import type { ButtonProps } from "antd/es/button";

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
  const [seconds, setSeconds] = useState(0);

  useEffect(() => {
    let id: NodeJS.Timeout;
    if (waiting) {
      id = setInterval(() => {
        setSeconds(sec => {
          if (sec <= 1) {
            setWaiting(false);
            clearInterval(id);
            return 0;
          } else
            return sec - 1;
        });
      }, 1000);
    }
    return () => clearInterval(id);
  }, [waiting]);

  const clicked = () => { setSeconds(interval); setWaiting(true); };

  return <Button loading={waiting} disabled={waiting} onClick={clicked} {...props}>
    {waiting ? `${seconds}秒` : text}
  </Button>;
};
