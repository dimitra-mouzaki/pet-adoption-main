const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');

const app = express();
const port = 7000;

// App is using static the current directory
app.use(cors());
app.use(express.static(__dirname));
app.use(bodyParser.json());

app.listen(port, () => {
    console.log(`Server is running on http://localhost:${port}`);
});
