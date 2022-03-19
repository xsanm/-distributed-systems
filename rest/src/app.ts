import express, {Application} from "express"
import path from "path";

import hbs from 'hbs';



const app: Application = express();

const PORT = 7007;

app.get('/', (req, res) => res.send("Hello world"));

app.get('/index', (request, response) => {
    response.render('index', {
        message: "siema"
    });
});


hbs.registerPartials(path.join(__dirname, '../views/partials'));

app.set('view engine', 'hbs');
app.listen(PORT, () => {
    console.log(`Listening on ${PORT}`);
});