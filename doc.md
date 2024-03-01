# Sensor API

This API provides endpoints to manage sensors and their data.

## Retrieve Sensor Details

### GET /sensors/:name

Retrieves the details of a sensor by its name.

**Parameters**
- `name`: The name of the plant.

**Example**
```bash
GET /sensors/rose
```

**Response**
```json
{
  "name": "rose",
  "ip": "192.168.1.100"
}
```

## Register Sensor

### POST /sensors/register

Registers a new sensor.

**Request Body**
```json
"192.168.1.100"
```

**Example**
```bash
POST /sensors/register
Content-Type: application/json

{
  "plant": "rose",
  "ip": "192.168.1.100"
}
```

**Response**
```json
"Sensor rose registered at 192.168.1.100"
```

## Trigger Sensor Update

### PUT /sensors/trigger

Update triggers on the sensor.

**Request Body**
```json
{
  "plant": "rose",
  "temperature_trigger": 25.5,
  "light_trigger": 1000,
  "humidity_trigger": 60,
  "moisture_trigger": 50
}
```

**Example**
```bash
PUT /sensors/triggers
Content-Type: application/json

{
  "plant": "rose",
  "temperature_trigger": 25.5,
  "light_trigger": 1000,
  "humidity_trigger": 60,
  "moisture_trigger": 50
}
```

## Water plant toggle

### PUT /sensors/water

Toggles on/off plant watering.

**Request Body**
```json
{
  "plant": "rose"
}
```

# Plant API

This API provides endpoints to manage plants and their data.

## Register Plant

### POST /plants/register

Registers a new plant.

**Request Body**
```json
{
  "name": "rose",
  "type": "flower"
}
```

**Example**
```bash
POST /plants/register
Content-Type: application/json

{
  "name": "rose",
  "type": "flower"
}
```

**Response**
```json
{
  "data_set": [],
  "name": "rose",
  "type": "flower",
  "planted": "May 31, 2023, 9:06:37 PM"
}
```

## Retrieve All Plants

### GET /plants/all

Retrieves details of all registered plants.

**Example**
```bash
GET /plants/all
```

**Response**
```json
[
  {
    "data_set": [
      {
        "date": "May 31, 2023, 8:57:21 PM",
        "temperature": 25.5,
        "humidity": 56.0,
        "light": 30.0,
        "moisture": 0.0
      }
    ],
    "name": "test",
    "type": "basil",
    "planted": "May 31, 2023, 8:56:23 PM"
  },
  {
    "data_set": [],
    "name": "rose",
    "type": "flower",
    "planted": "May 31, 2023, 9:06:37 PM"
  }
]
```

## Retrieve all plants info

### GET /plants/info

**Example**
```bash
GET /plants/info
```

**Response**
```json
[
  {
    "name": "test",
    "type": "basil",
    "planted": "May 31, 2023, 8:56:23 PM"
  },
  {
    "name": "rose",
    "type": "flower",
    "planted": "May 31, 2023, 9:06:37 PM"
  }
]
```

## Update Plant Data

### PUT /plants/update

Updates sensor data for a plant.

**Request Body**
```json
{
  "name": "rose",
  "temperature": 25.5,
  "humidity": 60,
  "light": 1000,
  "moisture": 50
}
```

**Example**
```bash
PUT /plants/update
Content-Type: application/json

{
  "name": "rose",
  "temperature": 25.5,
  "humidity": 60,
  "light": 1000,
  "moisture": 50
}
```

**Response**
```json
"Plant test updated"
```

## Retrieve Plant Data

### GET /plants/:name/timespan

Retrieves sensor data of a plant within a specified time range.

**Parameters**
- `name`: The name of the plant.
- `from`: The starting timestamp of the data range in milliseconds from UNIX 1970.
- `to`: The ending timestamp of the data range in milliseconds from UNIX 1970.

**Example**
```bash
GET /plants/rose/timespan?from=1684928722009&to=1784928822009
```

**Response**
```json
[
  {
    "date": "May 31, 2023, 8:57:21 PM",
    "temperature": 25.5,
    "humidity": 56.0,
    "light": 30.0,
    "moisture": 0.0
  }
]
```

### GET /plants/:name

Retrieves all data for a plant.

**Parameters**
- `name`: The name of the plant.

**Example**
```bash
GET /plants/rose
```

**Response**
```json
{
  "data_set": [
    {
      "date": "May 31, 2023, 8:57:21 PM",
      "temperature": 25.5,
      "humidity": 56.0,
      "light": 30.0,
      "moisture": 0.0
    }
  ],
  "name": "rose",
  "type": "flower",
  "planted": "May 31, 2023, 8:56:23 PM"
}
```

## Delete Plant

### DELETE /plants/:name

Deletes a plant.

**Parameters**
- `name`: The name of the plant to be deleted.

**Example**
```bash
DELETE /plants/rose
```

**Response**
```json
"Plant rose deleted successfully"
```