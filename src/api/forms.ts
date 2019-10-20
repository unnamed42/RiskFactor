import { request } from "./http";

async function fetch(payload: { name: string }) {
  return await request<Section>({
    url: `/form/section/${encodeURIComponent(payload.name)}`, method: "GET"
  });
}

export {
  fetch
};
