import { request, downloadAsFile } from "./http";

import {
  IdResponse,
  TaskBrief, SectionBrief, AnswerBrief, Section
} from "@/types";

/**
 * 获取Task的基础信息
 * @param taskId Task的id
 */
export const task = (taskId: number | string) =>
  request<TaskBrief>({ url: `/tasks/${taskId}` });

/**
 * 获取当前用户能访问的所有Task
 */
export const tasks = () =>
  request<TaskBrief[]>({ url: "/tasks" });

/**
 * 获取Task下属的所有分节的详细信息
 * @param taskId Task的id
 */
export const taskSections = (taskId: number | string) =>
  request<Section[]>({ url: `/tasks/${taskId}/sections` });

/**
 * 获取Task下属的所有分节的基础信息
 * @param taskId Task的id
 */
export const taskSectionNames = (taskId: number | string) =>
  request<SectionBrief[]>({ url: `/tasks/${taskId}/sections/name` });

/**
 * 获取提交给Task的所有问卷回复的基础信息
 * @param taskId Task的id
 */
export const taskAnswers = (taskId: number | string) =>
  request<AnswerBrief[]>({ url: `/tasks/${taskId}/answers` });

/**
 * 获取分节（Section）的完整信息
 * @param sectionId Section的id
 */
export const section = (sectionId: number | string) =>
  request<Section>({ url: `/sections/${sectionId}` });

export const answer = (answerId: number | string) =>
  request<any>({ url: `/answers/${answerId}/body` });

export const deleteAnswer = (answerId: number | string) =>
  request<void>({ url: `/tasks/0/answers/${answerId}`, method: "DELETE" });

export const downloadAnswer = (answerId: number | string) =>
  request<Blob>({ url: `/answers/${answerId}/file`, responseType: "blob" })
    .then(data => downloadAsFile(data, "export.json"));

export const postAnswerSection = (sectionId: number | string, value: any) =>
  request<IdResponse>({ url: `/answers/section/${sectionId}`, data: value, method: "POST" });

export const postAnswer = (taskId: number | string, value: any) =>
  request<IdResponse>({ url: `/tasks/${taskId}/answers`, data: value, method: "POST" });

export const updateAnswer = (answerId: number | string, patches: any) =>
  request<IdResponse>({ url: `/answers/${answerId}/body`, data: patches, method: "PUT" });
