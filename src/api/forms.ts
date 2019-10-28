import { request } from "./http";

export const getSection = async (payload: { name: string }) =>
  await request<Section>({
    url: `/form/section/${encodeURIComponent(payload.name)}`, method: "GET"
  });

export const getSections = async () =>
  await request<Sections>({ url: "/form", method: "GET" });
