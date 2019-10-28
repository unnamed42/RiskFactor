import { request } from "./http";

export const section = async (payload: { name: string }) =>
  await request<Section>({
    url: `/form/section/${encodeURIComponent(payload.name)}`, method: "GET"
  });

export const sectionsByName = async (payload: { name: string }) =>
  await request<Sections>({
    url: `/form/sections/${encodeURIComponent(payload.name)}`, method: "GET"
  });

export const sections = async () =>
  await request<Sections[]>({ url: "/form/sections", method: "GET" });
