import React, { ChangeEvent, forwardRef } from "react";

interface P {
  accept: string;
  onLoaded?: (buffer: ArrayBuffer) => void;
}

export const File = forwardRef<HTMLInputElement, P>(({ onLoaded, accept }, ref) => {
  const fileChosen = ({ target: { files } }: ChangeEvent<HTMLInputElement>) => {
    if(!files)
      return;
    const file = files[0], reader = new FileReader();
    reader.onloadend = () => {
      const content = reader.result as ArrayBuffer;
      onLoaded?.(content);
    };
    reader.readAsArrayBuffer(file);
  };

  return <input type="file" accept={accept} ref={ref} onChange={fileChosen}
                style={{ display: "none" }} />;
});
