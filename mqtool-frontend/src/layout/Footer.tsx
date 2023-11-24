import React from 'react';
import { Link } from 'react-router-dom';

function Footer() {
    return (
        <footer className="footer">
            <p className="foot-text">RabbitMQ Tool Version 1.0.0</p>
            <div className="foot-text">
        <Link to="mailto:max.mustermann@accenture.com?subject=RabbitMQ Tool Feedback" className='foot-text-link'>
          Feedback
        </Link>
      </div>
        </footer>
    );
}

export default Footer;