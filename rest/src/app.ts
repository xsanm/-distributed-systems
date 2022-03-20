import express, {Application} from "express"
import path from "path";


import hbs from 'hbs';

import axios from "axios";
import bodyParser from "body-parser";


const app: Application = express();

const PORT = 7007;

app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());
app.get('/', (req, res) => res.send("Hello world"));


let mess = "siema";

const deviceData = {
    deviceName: "",
    type: "",
    volume: "",
}

const songData = {
    title: "",
    artists: [],
    album: "",
    img: "",
}

type ArtistType = {
    name?: string;
    twitter_name?: string;
    facebook_name?: string;
    instagram_name?: string;
    image_url?: string;
    id: number;
}

let featuredArtists: ArtistType[] = [];
let mainArtists: ArtistType[] = [];
let producentArtists: ArtistType[] = [];


require('dotenv').config();

//const spotifyAuthToken = process.env.SPOTIFY_TOKEN;
const geniusAuthToken = process.env.GENIUS_TOKEN;

const fetchSpotifyData = async (spotifyAuthToken: number) => {
    const api_url = `https://api.spotify.com/v1/me/player`;

    try {
        const response = await axios.get(api_url, {
            headers: {
                'Authorization': `Bearer ${spotifyAuthToken}`,
                "Accept": "application/json",
                "Content-Type": "application/json",
            }
        });
        const song = response.data.item;
        const device = response.data.device;

        //console.log(response.status);

        songData.title = song?.name ?? "";
        songData.artists = song?.artists.map((artist: { name: any; }) => artist?.name);
        songData.album = song?.album?.name;
        songData.img = song?.album?.images?.[0]?.url;

        deviceData.deviceName = device?.name;
        deviceData.type = device?.type;
        deviceData.volume = device?.volume_percent;
    } catch (error) {
        console.log("Could not connect to spotify API");
    }
}

const fetchGeniusData = async () => {
    try {
    const searchPhrase = songData.title;
    const headers = {
        'Authorization': `Bearer ${geniusAuthToken}`,
        "Accept": "application/json",
        "Content-Type": "application/json",
    };

    const response = await axios.get("https://api.genius.com/search", {
        params: {
            'q': searchPhrase
        },
        headers
    });


    const response2 = await axios.get("https://api.genius.com/songs/" + response.data.response.hits?.[0].result.id, {
        headers
    });

    featuredArtists = response2.data.response.song.featured_artists.map((art: { id: string; }) => ({id: art.id}));
    mainArtists = [{id: response2.data.response.song.primary_artist.id ?? ""}];
    producentArtists = response2.data.response.song.producer_artists.map((art: { id: string; }) => ({id: art.id}));

    const artistsIds = [...mainArtists, ...featuredArtists, ...producentArtists];

    const artistsRawData = await Promise.all(
        artistsIds.map(
            (artistsIds) => axios.get("https://api.genius.com/artists/" + artistsIds.id, {headers})
        ));

    const artistData = artistsRawData.map(art => art.data.response.artist);

    featuredArtists = featuredArtists.map((art) => ({...artistData.filter(artData => artData.id == art.id)?.[0]}));
    mainArtists = mainArtists.map((art) => ({...artistData.filter(artData => artData.id == art.id)?.[0]}));
    producentArtists = producentArtists.map((art) => ({...artistData.filter(artData => artData.id == art.id)?.[0]}));
    } catch (error) {
        console.log("Could not connect to genius API");
    }
}


app.post('/index', async (request, response) => {


    await fetchSpotifyData(request.body.userToken);
    await fetchGeniusData();

    response.render('index', {
        title: songData.title,
        artist: songData.artists?.join(" "),
        album: songData.album,
        img: songData.img,
        deviceName: deviceData.deviceName,
        type: deviceData.type,
        volume: deviceData.volume,
        featuredArtists: featuredArtists,
        mainArtists: mainArtists,
        producentArtists: producentArtists
    });

});


hbs.registerPartials(path.join(__dirname, '../views/partials'));


app.set('view engine', 'hbs');
app.listen(PORT, () => {
    console.log(`Listening on ${PORT}`);
});