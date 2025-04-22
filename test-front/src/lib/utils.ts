import { Serializable } from "child_process";
import { UdataResult, ApiResult, ScriptError } from "./types";

export function udataScriptMessage(message: string): UdataResult
{
  return { message: message };
}

export function udataScriptResult(apiResult: ApiResult): UdataResult
{
  return { apiResult: apiResult };
}

export function udataScriptError(caller: string, message: string, error?: string): UdataResult
{
  return {
    scriptError: {
      caller: caller,
      message: message,
      error: error
    }
  };
}

export function processUdataResult(
  result: UdataResult,
  methodName: string,
  successHandler: (result: Serializable | Serializable[] | null) => void,
  errorHandler: (scriptError: ScriptError) => void
)
{
  if (result.apiResult)
  {
    if (result.apiResult.success)
    {
      successHandler(result.apiResult.result);
    }
    else
    {
      errorHandler({
        caller: methodName,
        message: result.apiResult.errorInfo?.errorName || "Unknown server side error",
        error: result.apiResult.errorInfo?.errorInfo || "Unknown server side error"
      });
    }

  }
  else if (result.scriptError)
  {
    errorHandler(result.scriptError);
  }
  else if (result.message)
  {
    errorHandler({
      caller: methodName,
      message: result.message,
    });
  }
  else
  {
    errorHandler({
      caller: methodName,
      message: `${methodName} returned result of ill format`,
      error: JSON.stringify(result)
    });
  }
}

export function processUdataResultPromise(
  resultPromise: Promise<UdataResult>,
  methodName: string,
  successHandler: (result: Serializable | Serializable[] | null) => void,
  errorHandler: (scriptError: ScriptError) => void
)
{
  resultPromise
    .then(result =>
    {
      processUdataResult(result, methodName, successHandler, errorHandler);
    })
    .catch(error =>
    {
      errorHandler({
        caller: methodName,
        message: `${methodName} request failed, returned error`,
        error: JSON.stringify(error)
      });
    });
}