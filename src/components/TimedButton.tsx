import React, { FC, MouseEventHandler } from "react";

import { Button } from "antd";
import { ButtonProps } from "antd/lib/button";

import { useStateAsync, sleep } from "@/utils";

type P = ButtonProps & {
  interval: number;
  text: string;
  onClick?: MouseEventHandler<HTMLElement>;
};

export const TimedButton: FC<P> = ({ interval, text, onClick, ...rest }) => {

  const [suspended, setSuspended] = useStateAsync(false);
  const [countDown, setCountDown] = useStateAsync(0);

  const clicked = async (e: React.MouseEvent<HTMLElement, MouseEvent>) => {
    if (onClick) onClick(e);
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
