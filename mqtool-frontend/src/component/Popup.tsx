import React, { useState } from "react";

interface PopupProps {
  isOpen: boolean;
  children: string;
}

const Popup: React.FC<PopupProps> = ({ isOpen, children }) => {
  const [isPopupOpen, setPopupOpen] = useState(isOpen);

  const closePopup = () => {
    setPopupOpen(false);
  };

  const overlayStyle: React.CSSProperties = {
    backgroundColor: "lightGray",
    //marginTop: "10%",
    position: "fixed",
    top: 0,
    left: 0,
    width: "100%",
    height: "100%",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
  };

  const contentStyle: React.CSSProperties = {
    backgroundColor: "white",
    width: 300,
    height: 200,
    fontSize: "medium",
    fontWeight: "bold",
    justifyContent: "center",
    //marginLeft: "40%",
    textAlign: "center",
    borderColor: "black",
    borderStyle: "ridge",
  };

  const buttonStyle: React.CSSProperties = {
    cursor: "pointer",
    marginLeft: "83%",
    marginBottom: 7,
  };

  return (
    <>
      {isPopupOpen && (
        <div className="overlay" style={overlayStyle}>
          <div className="content" style={contentStyle}>
            <button
              className="close-button"
              onClick={closePopup}
              style={buttonStyle}
            >
              Close
            </button>
            {children}
          </div>
        </div>
      )}
    </>
  );
};
export default Popup;

/**
 * here is an example on how to instanciate this component:#
 * 
 * <Route
              path="/popup"
              element={
                <Popup2
                  isOpen={true}
                  children="info message children of pop up"
                />
              }
            />
 */
