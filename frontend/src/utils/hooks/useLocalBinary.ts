import { useRef, useEffect, useCallback } from "react";

export const useLocalBinary = (filename: string) => {
  const href = useRef<HTMLAnchorElement>();

  useEffect(() => {
    const anchor = document.createElement("a");
    anchor.setAttribute("download", filename);
    anchor.setAttribute("style", "display: none");
    document.body.appendChild(anchor);
    href.current = anchor;

    return () => {
      if (href.current) document.body.removeChild(href.current);
    };
  }, [filename]);

  const download = useCallback((data: BlobPart[]) => {
    if (!href.current) return;
    const blob = new Blob(data, { type: "octet/stream" });
    const url = window.URL.createObjectURL(blob);
    href.current.setAttribute("href", url);
    href.current.click();
    window.URL.revokeObjectURL(url);
  }, []);

  return [download];
};
