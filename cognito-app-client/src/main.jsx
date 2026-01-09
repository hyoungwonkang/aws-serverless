import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'

import App from './App.jsx'
import { Amplify } from 'aws-amplify'
import awsConfig from './aws-config.js'

Amplify.configure(awsConfig);

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
