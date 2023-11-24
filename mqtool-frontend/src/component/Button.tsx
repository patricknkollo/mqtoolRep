import React from "react";

interface ButtonProps {
  color: string;
  type: "button" | "submit" | "reset";
  onClick: () => void;
  children: string;
  disable: boolean;
  width?: string;
  height?: string;
}

const Button: React.FC<ButtonProps> = ({
  color,
  children,
  onClick,
  disable,
  type,
  width,
  height,
}) => {
  const buttonStyle: React.CSSProperties = {
    backgroundColor: color,
    padding: "10px 15px",
    borderRadius: "5px",
    color: "#fff",
    cursor: "pointer",
    width: width,
    height: height,
  };

  return (
    <div>
      <div className="flex justify-end mt-5">
        <button
          type="button"
          onClick={onClick}
          color={type}
          disabled={disable}
          style={buttonStyle}
        >
          {children}
        </button>
      </div>
    </div>
  );
};

export default Button;

/**
 * here is an example of how to instaciate this component
 * 
 *  
          <Button
            color="yellow"
            type="button"
            onClick={handleButtonClick}
            children="save"
            disable={false}
          ></Button>
 */
