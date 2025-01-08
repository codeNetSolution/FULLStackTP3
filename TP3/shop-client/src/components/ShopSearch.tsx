import React, { useState } from 'react';
import { searchShops } from '../services/ShopService';
import { Shop } from '../types/shop';
import '../assets/ShopSerach.css';

const ShopSearch: React.FC = () => {
  const [name, setName] = useState<string>('');
  const [inVacations, setInVacations] = useState<boolean | undefined>(undefined);
  const [startDate, setStartDate] = useState<string>('');
  const [endDate, setEndDate] = useState<string>('');
  const [shops, setShops] = useState<Shop[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [currentPage, setCurrentPage] = useState<number>(1);
  const [pageSize] = useState<number>(10);
  const [error, setError] = useState<string | null>(null);

  const handleSearch = async () => {
    if (startDate && endDate && new Date(startDate) > new Date(endDate)) {
      setError('Start date cannot be later than end date.');
      return;
    }

    setLoading(true);
    setError(null);
    try {
      const results = await searchShops(name, inVacations, startDate, endDate);
      setShops(results);
    } catch (error) {
      console.error('Error fetching shops:', error);
      setError('Failed to fetch shops. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  const totalPages = Math.ceil(shops.length / pageSize);
  const paginatedShops = shops.slice((currentPage - 1) * pageSize, currentPage * pageSize);

  return (
    <div className="shop-search-container">
      <h1 className="shop-search-header">Search Shops</h1>

      <div className="shop-search-form">
        <input
          type="text"
          placeholder="Search by name"
          value={name}
          onChange={(e) => setName(e.target.value)}
          className="shop-search-input"
        />
        <select
          value={inVacations === undefined ? '' : inVacations ? 'true' : 'false'}
          onChange={(e) =>
            setInVacations(e.target.value === '' ? undefined : e.target.value === 'true')
          }
          className="shop-search-select"
        >
          <option value="">All</option>
          <option value="true">In Vacations</option>
          <option value="false">Not in Vacations</option>
        </select>
        <input
          type="date"
          value={startDate}
          onChange={(e) => setStartDate(e.target.value)}
          className="shop-search-input"
        />
        <input
          type="date"
          value={endDate}
          onChange={(e) => setEndDate(e.target.value)}
          className="shop-search-input"
        />
        <button onClick={handleSearch} className="shop-search-button">
          Search
        </button>
      </div>

      {error && <p className="shop-search-error">{error}</p>}

      {loading ? (
        <p>Loading...</p>
      ) : (
        <>
          <table className="shop-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>In Vacations</th>
                <th>Created At</th>
                <th>Number of Products</th>
              </tr>
            </thead>
            <tbody>
              {paginatedShops.map((shop) => (
                <tr key={shop.id}>
                  <td>{shop.id}</td>
                  <td>{shop.name}</td>
                  <td>{shop.inVacations ? 'Yes' : 'No'}</td>
                  <td>{shop.createdAt}</td>
                  <td>{shop.nbProducts}</td>
                </tr>
              ))}
            </tbody>
          </table>

          <div className="shop-pagination">
            <button
              onClick={() => setCurrentPage((prev) => Math.max(prev - 1, 1))}
              disabled={currentPage === 1}
            >
              Previous
            </button>
            <span>
              Page {currentPage} of {totalPages}
            </span>
            <button
              onClick={() => setCurrentPage((prev) => Math.min(prev + 1, totalPages))}
              disabled={currentPage === totalPages}
            >
              Next
            </button>
          </div>
        </>
      )}
    </div>
  );
};

export default ShopSearch;
