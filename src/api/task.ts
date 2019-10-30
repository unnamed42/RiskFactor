import { request } from "./http";

import { TaskBrief, SectionBrief, AnswerBrief } from "@/types/task";

export * from "@/types/task";

export const task = async (taskId: number | string) =>
  await request<TaskBrief>({ url: `/task/${taskId}` });

export const tasks = async () =>
  await request<TaskBrief[]>({ url: "/tasks" });

export const taskSections = async (taskId: number | string) =>
  await request<SectionBrief[]>({ url: `/task/${taskId}/sections` });

export const taskAnswers = async (taskId: number | string) =>
  await request<AnswerBrief[]>({ url: `/task/${taskId}/answers` });
