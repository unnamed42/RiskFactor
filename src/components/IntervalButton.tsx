import React, { FC } from "react";

import { Button } from "antd";
import { ButtonProps } from "antd/lib/button";

import { useStateAsync, sleep } from "@/utils";

interface IntervalButtonProps extends ButtonProps {
  interval: number;
  text: string;
  onClick?: React.MouseEventHandler<HTMLElement>;
}

export const IntervalButton: FC<IntervalButtonProps> = props => {

  const [suspended, setSuspended] = useStateAsync(false);
  const [countDown, setCountDown] = useStateAsync(0);

  const { interval, text, onClick, ...rest } = props;

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
