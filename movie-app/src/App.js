import React, { useState } from 'react';
import axios from 'axios';
import DataTable from 'react-data-table-component';
import Select from 'react-select';
import './App.css';

const DataTableContainer = ({ request, onError }) => {
    const [data, setData] = useState([]);
    const [columns, setColumns] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const fetchData = async (requestUrl) => {
        setLoading(true);
        try {
            const response = await axios.get(requestUrl);
            const fetchedData = response.data;

            if (Array.isArray(fetchedData) && fetchedData.length > 0) {
                const keys = Object.keys(fetchedData[0]);
                const dynamicColumns = keys.map(key => ({
                    name: key,
                    selector: row => row[key],
                }));

                setData(fetchedData);
                setColumns(dynamicColumns);
            } else {
                const keys = Object.keys(fetchedData);
                const dynamicColumns = keys.map(key => ({
                    name: key,
                    selector: row => row[key],
                }));

                setData([fetchedData]);
                setColumns(dynamicColumns);
            }
            setError(null);
        } catch (error) {
            if (error.response && error.response.status === 500) {
                setError("Server error. Please check your input and try again.");
            } else if (error.response && error.response.status === 404) {
                setError("Data not found. Please check your input and try again.");
            } else {
                setError("An unexpected error occurred. Please try again.");
            }
            onError(error.message); 
        } finally {
            setLoading(false);
        }
    };

    React.useEffect(() => {
        if (request) {
            fetchData(request);
        }
    }, [request]);

    if (loading) return <div>Loading...</div>;
    if (error) return <div>Error: {error}</div>;

    return (
        <div className='TableContainer'>
            <DataTable
                columns={columns}
                data={data}
            />
        </div>
    );
};

const DropDownSelector = ({ onSelect }) => {
    const options = [
        { label: 'Get Movie TMDB ID', value: 0 },
        { label: 'Get Person Info', value: 1 },
        { label: 'Whose played on the movie?', value: 2 },
        { label: 'Whose worked on the movie?', value: 3 },
        { label: 'What movies the actor played in?', value: 4 },
        { label: 'What movies the set worker worked in?', value: 5 },
    ];

    return (
        <div className='DropDownMenu'>
            <Select
                options={options}
                onChange={(opt) => onSelect(opt ? opt.value : '')}
            />
        </div>
    );
}

function App() {
    const [request, setRequest] = useState('');
    const [id, setId] = useState('');
    const [name, setName] = useState('');
    const [question, setQuestion] = useState('');
    const [error, setError] = useState('');

    const handleIdChange = (e) => {
        setId(e.target.value);
    }

    const handleNameChange = (e) => {
        setName(e.target.value);
    }

    const handleSelection = (val) => {
        setQuestion(val);
    }

    const sendRequest = () => {
        if (question === '') {
            setError("Please select an option.");
            return;
        }

        let url = '';
        if (question === 1 || question === 4 || question === 5) {
            if (!id && !name) {
                setError("Please provide either ID or Name for Cast and Crew Info.");
                return;
            }
            else if (id && name) {
                setError("Please choose one either ID or Name.")
            }
            url = `http://localhost:8080/${["movie", "person", "movieActors", "movieCrew", "played", "worked"][question]}?id=${id}`;
        } else {
            // Other options
            if (!id) {
                setError("Please provide an ID.");
                return;
            }
            url = `http://localhost:8080/${["movie", "person", "movieActors", "movieCrew", "played", "worked"][question]}?id=${id}`;
        }

        setRequest(url);
        setError('');
    }

    return (
        <div className="App">
            <div className="controls">
                <h2>Search Data</h2>
                <input
                    type="text"
                    value={id}
                    onChange={handleIdChange}
                    placeholder="Enter ID"
                />
                <input
                    type="text"
                    value={name}
                    onChange={handleNameChange}
                    placeholder="Enter Name"
                    disabled={[0,2,3].includes(question)}
                />
                <DropDownSelector onSelect={handleSelection} />
                <button onClick={sendRequest}>Send Request</button>
                {error && <div className="error-message">{error}</div>}
            </div>
            <DataTableContainer request={request} onError={setError} />
        </div>
    );
}

export default App;
