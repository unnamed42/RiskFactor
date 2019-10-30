import { request } from "./http";

import { TaskBrief, SectionBrief, AnswerBrief } from "@/types/task";

export * from "@/types/task";

/**
 * 获取Task的基础信息
 * @param taskId Task的id
 */
export const task = async (taskId: number | string) =>
  await request<TaskBrief>({ url: `/task/${taskId}` });

/**
 * 获取当前用户能访问的所有Task
 */
export const tasks = async () =>
  await request<TaskBrief[]>({ url: "/tasks" });

/**
 * 获取Task下属的所有分节的基础信息
 * @param taskId Task的id
 */
export const taskSections = async (taskId: number | string) =>
  await request<SectionBrief[]>({ url: `/task/${taskId}/sections` });

/**
 * 获取提交给Task的所有问卷回复的基础信息
 * @param taskId Task的id
 */
export const taskAnswers = async (taskId: number | string) =>
  await request<AnswerBrief[]>({ url: `/task/${taskId}/answers` });
