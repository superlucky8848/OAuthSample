'use client';

import { publicHello } from "@/lib/api-common";
import { processUdataResultPromise } from "@/lib/utils";

export default function PublicPage()
{
  function sayHello()
  {
    processUdataResultPromise(
      publicHello("PublicPage"),
      "processUdataResultPromise",
      (result) => alert(result as string),
      (error) => alert(`Error: ${JSON.stringify(error)}`)
    );
  }

  return (
    <div>
      <h2>Public Page</h2>
      <div>
        <button
          className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
          onClick={sayHello}
        >
          hello
        </button>
      </div>
    </div>
  );
}