import {
  FormGroup,
  FieldControl,
  FormBuilder,
  Validators,
  FieldGroup,
} from "react-reactive-form";

interface FormValue {
  data: string;
}

interface BackendResponse {}

const InputFormReactive: React.FC = () => {
  const form: FormGroup = FormBuilder.group({
    data: ["", Validators.required],
  });

  const handleSubmit = async (
    event: React.FormEvent<HTMLFormElement>
  ): Promise<void> => {
    event.preventDefault();
    if (form.valid) {
      try {
        const response: BackendResponse = await submitDataToBackend(form.value);

        console.log(response);
      } catch (error) {
        if (error instanceof Error) {
          console.error(error.message);
        }
      }
    }
  };

  return (
    <div className="p-4">
      <FieldGroup
        control={form}
        render={({ get, invalid }) => (
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="flex flex-col space-y-2">
              <FieldControl
                name="data"
                render={({ handler, touched, hasError }) => (
                  <>
                    <div className="flex flex-row space-x-2">
                      <input
                        {...handler()}
                        id="data"
                        className="border p-2 rounded w-1/2"
                        type="text"
                        required
                      />
                      <button
                        type="submit"
                        className="bg-blue-500 text-white p-2 rounded disabled:bg-blue-300"
                        disabled={invalid}
                      >
                        Submit
                      </button>
                    </div>
                    {touched && hasError("required") && (
                      <span className="text-red-500 block">
                        This field is required
                      </span>
                    )}
                  </>
                )}
              />
            </div>
          </form>
        )}
      />
    </div>
  );
};

export default InputFormReactive;

async function submitDataToBackend(data: FormValue): Promise<BackendResponse> {
  await new Promise((resolve) => setTimeout(resolve, 1000));
  console.log("Data to be submitted:", data);

  return {
    message: "submitted succesfully",
  };
}
