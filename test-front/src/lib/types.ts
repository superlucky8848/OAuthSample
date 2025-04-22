import { Serializable } from "child_process";

export type ApiResult = {
    success: boolean,
    errorInfo: {
        errorName: string,
        errorInfo: string
    } | null,
    result: Serializable | Serializable[] | null
};

export type ScriptError = {
    caller: string,
    message: string,
    error?: string
};

export type UdataResult = {
    message?: string,
    apiResult?: ApiResult,
    scriptError?: ScriptError
    blob?: Blob
};