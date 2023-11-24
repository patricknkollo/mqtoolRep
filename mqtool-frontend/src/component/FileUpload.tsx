interface FileUpload {
  onChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
}

const FileUpload: React.FC<FileUpload> = ({ onChange }) => {
  return (
    <div>
      <input
        type="file"
        id="textfile"
        name="textfile"
        accept="text/*"
        onChange={onChange}
      />
    </div>
  );
};

export default FileUpload;
