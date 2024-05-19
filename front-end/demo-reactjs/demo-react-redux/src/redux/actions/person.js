import { ADDPERSON } from "../const";

export const createAddPersonAction = (personObj) => ({ type: ADDPERSON, data: personObj })
