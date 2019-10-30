export type QType = "TEXT" | "NUMBER" | "DATE" | "CHOICE" | "IMMUTABLE" |
  "YESNO_CHOICE" | "LIST" | "LIST_APPENDABLE" |
  "MULTI_CHOICE" | "SINGLE_CHOICE" |
  "MULTI_SELECT" | "SINGLE_SELECT";

export interface Question {
  type: QType;
  field: string;
  label?: string;
  list?: Question[];

  required?: boolean;
  isEnabler?: boolean;
  yesno?: string;
  addonPosition?: "prefix" | "postfix";
  selected?: string;
  description?: string;
  placeholder?: string;
  filterKey?: string;
}

export interface SectionBrief {
  id: number;
  title: string;
}

export interface Section extends SectionBrief {
  sections?: Section[];
  questions?: Question[];
}

export interface TaskBrief {
  id: number;
  center: string;
  name: string;
  mtime: string;
}

export interface Task extends TaskBrief {
  sections?: Section[];
  questions?: Question[];
}

export interface AnswerBrief {
  id: number;
  creator: string;
  mtime: string;
}
