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

export interface Section {
  title: string;
  sections?: Section[];
  questions?: Question[];
}

export interface Task {
  id: number;
  center: string;
  name: string;
  mtime: string;
  sections: Section[];
}

export interface AnswerBrief {
  id: number;
  creator: string;
  mtime: string;
}
