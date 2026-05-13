export default function ErrorBanner({ error, success }) {
    return (
        <div>
            {error ? <p className="error">{error}</p> : null}
            {success ? <p className="success">{success}</p> : null}
        </div>
    );
}
