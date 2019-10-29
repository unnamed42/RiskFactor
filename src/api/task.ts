import { request } from "./http";

export const tasks = async () =>
  await request<Task[]>({ url: "/task" });

export const taskSections = async (taskId: number) =>
  await request<Section[]>({ url: `/task/${taskId}/sections` });

export const sectionTitles = async (taskId: number) =>
  await request<string[]>({ url: `/task/${taskId}/section-names` });

export const answers = async () =>
  await request<AnswerBrief[]>({ url: `/answer` });
