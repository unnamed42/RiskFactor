import React, { FC } from "react";

import { Button } from "antd";
import { ButtonProps } from "antd/lib/button";

import { useStateAsync, sleep } from "@/utils";

type P = ButtonProps & {
  interval: number;
  text: string;
};

/**
 * 按钮按下后，等待一段时间才能再次click
 * 可用于请求验证码的按钮，请求一次之后60s以内不能再次请求
 */
export const TimedButton: FC<P> = ({ interval, text, onClick, ...rest }) => {

  const [suspended, setSuspended] = useStateAsync(false);
  const [countDown, setCountDown] = useStateAsync(0);

  const clicked = async (e: React.MouseEvent<HTMLElement, MouseEvent>) => {
    if(suspended)
      return;
    await setSuspended(true);
    let countdown = await setCountDown(interval);
    while (countdown !== 0) {
      await sleep(1000);
      countdown = await setCountDown(countdown - 1);
    }
    await setSuspended(false);
  };

  return (
    <Button {...rest} disabled={suspended} loading={suspended}
            onClick={clicked}>
      { suspended ? `${countDown}秒` : text }
    </Button>
  );
};
