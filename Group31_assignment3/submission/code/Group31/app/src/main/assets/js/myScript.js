

function plotGraph(dataPoints1,dataPoints2,dataPoints3)
{
  var x = [];
  var y = [];
  var z = [];
  var col;
  col = 'blue';
  var data = [];
       var x = [];
       var y = [];
       var z = [];
         var trace1 = {type: 'scatter3d',
         mode:'lines',
         name:'Running',
         line:{reversescale: false},
         marker:{color:col}};
  for (i in dataPoints1)
  {

    for(point in dataPoints1[i])
    {
      //console.log(dataPoints[i][point].x);
      x.push(dataPoints1[i][point].x);
      y.push(dataPoints1[i][point].y);
      z.push(dataPoints1[i][point].z);
    }
/*
    trace1['x'] = x;
    trace1['y'] = y;
    trace1['z'] = z;
    //console.log(x.length);
    data.push(trace1);*/
  }
  console.log(data.length);
    trace1['x'] = x;
    trace1['y'] = y;
    trace1['z'] = z;
    //console.log(x.length);
    data.push(trace1);
  //Plotly.newPlot(myDiv, data);

  col = 'red';

  //var data = [];
     var x = [];
     var y = [];
     var z = [];
    var trace2 = {type: 'scatter3d',
    mode:'lines',
    name:'Jumping',
    line:{reversescale: false},
    marker:{color:col}};
  for (i in dataPoints2)
  {
    for(point in dataPoints2[i])
    {
      //console.log(dataPoints[i][point].x);
      x.push(dataPoints2[i][point].x);
      y.push(dataPoints2[i][point].y);
      z.push(dataPoints2[i][point].z);
    }
/*
    trace2['x'] = x;
    trace2['y'] = y;
    trace2['z'] = z;
    //console.log(x.length);
    data.push(trace2);*/
  }
  console.log(data.length);
    trace2['x'] = x;
    trace2['y'] = y;
    trace2['z'] = z;
    //console.log(x.length);
    data.push(trace2);
  //Plotly.newPlot(myDiv, data);
  col = 'green';

  //var data = [];
    var trace3 = {type: 'scatter3d',
    mode:'lines',
    name:'Walking',
    line:{reversescale: false},
    marker:{color:col}};
    var x = [];
    var y = [];
    var z = [];
  for (i in dataPoints3)
  {

    for(point in dataPoints3[i])
    {
      //console.log(dataPoints[i][point].x);
      x.push(dataPoints3[i][point].x);
      y.push(dataPoints3[i][point].y);
      z.push(dataPoints3[i][point].z);
    }
/*
    trace3['x'] = x;
    trace3['y'] = y;
    trace3['z'] = z;
    //console.log(x.length);
    data.push(trace3);
    x = [];
    y = [];
    z = [];*/
  }
  console.log(data.length);
  for (i in data){
    for(j in data[i])
    console.log(data[i][j])
  }
    trace3['x'] = x;
    trace3['y'] = y;
    trace3['z'] = z;
    //console.log(x.length);
    data.push(trace3);

  Plotly.plot(myDiv, data);

}