# frozen_string_literal: true

module Admin
  class ExportsController < Admin::ApplicationController
    def create
      render :index
    end
    def index
      key = params['discog']
      if key.to_s != ''
        Discog.write_json
        Album.write_json(key)
        Musician.write_json(key)
        Track.write_json(key)
        @key = key
        render :written
      end
    end
  end

end
