import { useRef, useEffect, useState, useCallback } from "react";

import { http } from "@/api";

interface Options {
  extensions?: string;
  multiple?: boolean;
}

const submitFile = async (url: string, files: FileList) => {
  const data = new FormData();
  for (let i = 0; i < files.length; ++i)
    data.append("file", files[i]);
  await http.post(url, data, {
    headers: { "Content-Type": "multipart/form-data" }
  });
};

/**
 * @return 返回第一个是所选文件列表，第二个是触发文件选择框，第三个是发送文件至`url`
 */
export const useFileChooser = ({
  extensions,
  multiple = false
}: Options): [FileList | null, () => void, (url: string) => Promise<void>] => {

  const input = useRef<HTMLInputElement>();
  const [selected, setSelected] = useState<FileList | null>(null);

  const submit = useCallback(async (url: string) => {
    if (selected != null)
      await submitFile(url, selected);
  }, [selected]);

  useEffect(() => {
    const selector = document.createElement("input");
    selector.setAttribute("type", "file");
    selector.addEventListener("change", function () {
      if (!this.files) return;
      setSelected(this.files);
    });
    if (extensions)
      selector.setAttribute("accept", extensions);
    if(multiple)
      selector.setAttribute("multiple", "multiple");
    selector.style.cssText = "visibility:hidden";
    input.current = selector;
  }, [extensions, multiple]);

  return [selected, () => input.current?.click(), submit];
};
