const express = require('express');
const mongoose = require('mongoose');
const app = express();

app.use(express.json());

const url = 'mongodb+srv://arponamitroy012:jqaPUF3LNhjpAbR7@cluster0.js0bs.mongodb.net/Students?retryWrites=true&w=majority';

mongoose.connect(url, {useNewUrlParser : true, useUnifiedTopology : true})
.then(()=>console.log('Connected to mongodb database'))
.catch(err=>console.error('Failed to connect with mongodb database: ', err));

const pathologicalTestSchema = new mongoose.Schema({
  
  title : String,
  cost : Number,
  isAvaiable : Boolean,
  reagent : String
  
}, {collection : 'PathologicalTest'});

const radiologicalTestSchema = new mongoose.Schema({
  
  title : String,
  cost : Number,
  isAvaiable : Boolean,
  plateDimention : String
  
}, {collection : 'RadioLogicalTest'});

const PathologicalTest = mongoose.model('PathologicalTest', pathologicalTestSchema ,'PathologicalTest');

const RadioLogicalTest = mongoose.model('RadioLogicalTest', radiologicalTestSchema , 'RadioLogicalTest');

app.post('/pathologicalTest', async(req, res)=>{
  
  console.log('received data :- ',req.body);
  
  const pathologicalTest = new PathologicalTest(req.body);
  
  try {
    
    await pathologicalTest.save();
    
    res.status(200).send(pathologicalTest);
    
    console.log('data is added in the pathological test database.');
    
  } catch(error) {
    
    console.log('error at adding data in pathological test :- ',error);
    
    res.status(400).send(error);
    
  }
  
});

app.post('/radioLogicalTest', async(req, res)=>{
  
  console.log('received data :- ', req.body);
  
  try {
    
    const radioLogicalTest = new RadioLogicalTest(req.body);
    
    await radioLogicalTest.save();
    
    res.status(201).send(radioLogicalTest);
    
    console.log('data is added in radiological test database');
    
  } catch(error) {
    
    console.log('error at adding data in radiological test :- ',error);
    
    res.status(400).send(error);
    
  }
  
});


app.get('/pathologicalTest/all', async(req, res)=>{
  
  try {
    
    const pathologicalTest = await PathologicalTest.find();
    
    res.status(201).send(pathologicalTest);
    
    console.log(pathologicalTest);
    
  } catch(error) {
    
    console.log('error at reading pathological test :- ',error);
    
  }
  
});

app.get('/pathologicalTest/search', async(req, res)=>{
  
  const {title} = req.query;
  
  try {
    
    if(!title) {
      
      return res.status(400).send('Title query parameter is required');
      
    }
    
    const pathologicalTest = await PathologicalTest.find({title : new RegExp(title, 'i')});
    
    res.status(200).send(pathologicalTest);
    
    console.log(pathologicalTest);
    
  } catch(error) {
    
    res.status(500).send(error);
    
    console.log(error);
    
  }
  
});

app.get('/pathologicalTest/cost', async(req, res)=>{
  
  const {cost} = req.query;
  
  try {
    
    if(!cost) {
      
      return res.status(400).send('cost query parameter is required.');
      
    }
    
    const numericCost = parseFloat(cost);
    
    if(isNaN(numericCost)) {
      
      return res.status(400).send('Invalid number input at here.');
      
    }
    
    const pathologicalTest = await PathologicalTest.find({cost: {$lte : numericCost}});
    
    res.status(200).send(pathologicalTest);
    
    console.log(pathologicalTest);
    
  } catch(error) {
    
    console.log(error);
    res.status(500).send(error);
    
    
  }
  
  
});

app.get('/radioLogicalTest/all', async(req, res)=>{
  
  try {
    
    const radioLogicalTest = await RadioLogicalTest.find();
    
    res.status(201).send(radioLogicalTest);
    
    console.log(radioLogicalTest);
    
  } catch(error) {
    
    console.log('error for read the RadioLogicalTest is :- ', error);
    
    res.status(500).send(error);
    
  }
  
});

app.get('/radioLogicalTest/search', async(req, res)=>{
  
  const {title} = req.query;
  
  try {
    
    if(!title) {
      
      return res.status(400).send('Title query parameter is required');
      
    }
    
    const radioLogicalTest = await RadioLogicalTest.find({title : new RegExp(title, 'i')});
    
    res.status(200).send(radioLogicalTest);
    
    console.log(radioLogicalTest);
    
  } catch(error) {
    
    res.status(500).send(error);
    
    console.log(error);
    
  }
  
});

app.get('/radioLogicalTest/cost', async(req, res)=>{
  
  const {cost} = req.query;
  
  try {
    
    if(!cost) {
      
      return res.status(400).send('cost query parameter is required.');
      
    }
    
    const numericCost = parseFloat(cost);
    
    if(isNaN(numericCost)) {
      
      return res.status(400).send('Invalid number input at here.');
      
    }
    
    const radiologicalTest = await RadioLogicalTest.find({cost: {$lte : numericCost}});
    
    res.status(200).send(radiologicalTest);
    
    console.log(radiologicalTest);
    
  } catch(error) {
    
    console.log(error);
    res.status(500).send(error);
    
    
  }
  
  
});

const port = process.env.PORT || 3000;
app.listen(port, ()=>{
  
  console.log(`server is running on ${port}`);
  
});

